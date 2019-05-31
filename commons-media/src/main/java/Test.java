/**
 * Copyright (C)
 * Vm Option -Djava.library.path=$PROJECT_DIR$\opencv\x64
 * Author: jameslinlu
 */


import java.io.File;
import java.util.*;
import java.util.Arrays;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_ml.*;
import static org.bytedeco.javacpp.opencv_objdetect.HOGDescriptor;


public class Test {


    public static void extractHog(Map<Integer, File[]> fileCategory, HOGDescriptor hogDescriptor, CvMat dataCvMat, CvMat labelCvMat) {
        int imgIndex = 0;
        int filesLen = 0;
        List<File> files = new ArrayList<>();
        for (Map.Entry<Integer, File[]> fileKv : fileCategory.entrySet()) {
            int flag = fileKv.getKey();
            files.addAll(new ArrayList(Arrays.asList(fileKv.getValue())));
            filesLen += fileKv.getValue().length;
            System.out.println("category = " + flag + ", file length = " + fileKv.getValue().length + " , total files length = " + files.size());
            for (; imgIndex < filesLen; imgIndex++) {
                File file = files.get(imgIndex);
                //读取图片转换为Mat
                String imgFilePath = file.getAbsolutePath();
                System.out.println("Scanner File " + imgIndex + " = " + imgFilePath);
                Mat img = imread(imgFilePath);
                //样本分类标示  1,-1
                //图片大小归一
                if (flag == 1) {//正样本
                    resize(img, img, new Size(64, 128));
                    cvtColor(img, img, COLOR_BGR2GRAY);
                }
                //按图片大小获取维度向量  初始化总体图片向量
                float[] hogVectorStores = new float[1 * (int) hogDescriptor.getDescriptorSize()];
                //计算特征向量
                hogDescriptor.compute(img, hogVectorStores);
                for (int hogVectorIdx = 0; hogVectorIdx < hogVectorStores.length; hogVectorIdx++) {
                    cvmSet(dataCvMat, imgIndex, hogVectorIdx, hogVectorStores[hogVectorIdx]);
                }
                cvmSet(labelCvMat, imgIndex, 0, flag);
            }
        }
        ;
    }

    public static HOGDescriptor getHogDescriptor() {
        //设置HOG提取算法
        Size winSize = new Size(64, 128);
        Size blockSize = new Size(16, 16);
        Size blockStride = new Size(8, 8);
        Size cellSize = new Size(8, 8);
        int nbins = 9;
        return new HOGDescriptor(winSize, blockSize, blockStride, cellSize, nbins);
    }

    public static void train() {

        //设置HOG提取算法
        HOGDescriptor hogDescriptor = getHogDescriptor();
        //正样本 读取70x134图片 E:\INRIAPerson\70X134H96\Test\pos
        //负样本 读取 随机大小图片 E:\INRIAPerson\Test\neg
        File[] filesPos = new File("E:\\INRIAPerson\\Train\\pos1").listFiles();
        File[] filesNes = new File("E:\\INRIAPerson\\Train\\neg1").listFiles();
        int IMG_NUM = filesPos.length + filesNes.length + 1;
        int HOG_VECTOR = (int) hogDescriptor.getDescriptorSize();

        //样本矩阵 = 图片数量 | Hog特征量 | 格式
        Mat dataMat = new Mat(IMG_NUM, HOG_VECTOR, CV_64FC1);
        System.out.println("Data Mat Cols = " + dataMat.cols() + " Rows = " + dataMat.rows());
        CvMat dataCvMat = new CvMat(dataMat);
        //样本类型矩阵
        Mat labelMat = new Mat(IMG_NUM, 1, CV_64FC1);
        System.out.println("Label Mat Cols = " + labelMat.cols() + " Rows = " + labelMat.rows());
        CvMat labelCvMat = new CvMat(labelMat);

        Map<Integer, File[]> map = new HashMap<>();
        map.put(1, filesPos);
        map.put(0, filesNes);

        extractHog(map, hogDescriptor, dataCvMat, labelCvMat);

        System.out.println("Begin Training " + new Date());
        Mat trainMat = new Mat(dataCvMat);
        trainMat.convertTo(trainMat, CV_32FC1);
        Mat categoryMat = new Mat(labelCvMat);
        categoryMat.convertTo(categoryMat, CV_32SC1);
        TrainData trainData = TrainData.create(trainMat, ROW_SAMPLE, categoryMat);

        SVM svm = SVM.create();
        svm.setType(SVM.C_SVC);
        svm.setKernel(SVM.LINEAR);
        svm.train(trainData);
        svm.save("D:/test.xml");
        Mat detectMat = svm.getSupportVectors();//返回Mat类型，获取SVM的支持向量
        System.out.println("SVM Vector Cols=" + detectMat.cols() + " Rows=" + detectMat.rows());
        Mat peopleMat = new Mat(HOGDescriptor.getDefaultPeopleDetector());
        System.out.println("People Vector Cols=" + peopleMat.cols() + " Rows=" + peopleMat.rows());
        System.out.println("SVM Vector Count = " + svm.getVarCount());//获取SVM支持向量的维数
        System.out.println("End Training " + new Date());
    }

    public static void predict() {
        // http://blog.csdn.net/pb09013037/article/details/41256945
        // http://www.aiuxian.com/article/p-2535356.html

        System.out.println("Loaded Begin " + new Date());
//        SVM svm = SVM.create();
//        FileStorage fsr = new FileStorage("D:/test.xml", FileStorage.READ);
//        svm.read(fsr.getFirstTopLevelNode());
//        Mat vectorMat = svm.getSupportVectors();
//        System.out.println("Vector Cols=" + vectorMat.cols() + " Rows=" + vectorMat.rows());
//        System.out.println("VectorCount = " + svm.getVarCount());


        File[] filesPos = new File("E:\\INRIAPerson\\Train\\pos1").listFiles();
        File[] filesNes = new File("E:\\INRIAPerson\\Train\\neg1").listFiles();
//        File[] filesPos = new File("E:\\INRIAPerson\\Test\\pos").listFiles();
//        File[] filesNes = new File("E:\\INRIAPerson\\Test\\neg").listFiles();
        List<File> files = new ArrayList<>();
        files.addAll(java.util.Arrays.asList(filesPos));
        files.addAll(java.util.Arrays.asList(filesNes));
        HOGDescriptor hog = getHogDescriptor();
        hog.setSVMDetector(new Mat(HOGDescriptor.getDefaultPeopleDetector()));
        for (File file : files) {
            String filePath = file.getAbsolutePath();
            Mat query = imread(filePath);
            RectVector vector = new RectVector();
            hog.detectMultiScale(query, vector);
            System.out.println("Detect " + filePath);
            if (vector.size() > 0) {
                String dst = "E:\\INRIAPerson\\Detect\\" + file.getName();
                imwrite(dst, query);
                Mat result = imread(dst);
                for (int i = 0; i < vector.size(); i++) {
                    Rect r = vector.get(i);
                    System.out.println("#### x = " + r.x() + " y = " + r.y() + " w = " + r.width() + " h = " + r.height() + " " + filePath);
                    rectangle(result, new Point(r.x(), r.y()), new Point(r.x() + r.width(), r.y() + r.height()), new Scalar(2, 255, 0, 0));
                }
            }

//            resize(query, query, new Size(64, 128));
//            Mat grey = new Mat();
//            cvtColor(query, grey, COLOR_BGR2GRAY);
//            imwrite("E:\\INRIAPerson\\myTest1\\"+file.getName(),query);
//            Mat dataMat = new Mat(vectorMat.rows(), vectorMat.cols(), CV_32FC1);
//            System.out.println("Data Mat Cols = " + dataMat.cols() + " Rows = " + dataMat.rows());
//            CvMat dataCvMat = new CvMat(dataMat);
//            float[] hogVectorStores = new float[vectorMat.rows() * vectorMat.cols()];
//            hog.compute(query, hogVectorStores, new Size(8, 8), new Size(8, 8), new PointVector());
//            for (int hogVectorIdx = 0; hogVectorIdx < hogVectorStores.length; hogVectorIdx++) {
//                cvmSet(dataCvMat, 0, hogVectorIdx, hogVectorStores[hogVectorIdx]);
//            }
//            System.out.println("Query Cols=" + dataMat.cols() + " Rows=" + dataMat.rows());
//            Mat trainMat = new Mat(dataCvMat);
//            float response = svm.predict(trainMat);
//            System.out.println("分类 " + response + "   " + filePath);
//            //https://github.com/mahto56/Face-detect-opencv3
//            RectVector rectVector = new RectVector();
//            hog.detectMultiScale(query, rectVector);
//            System.out.println("Rect Num= " + rectVector.size());
        }

        System.out.println("Loaded End" + new Date());
    }

    public static void main(String[] args) {
//        train();
        predict();

        System.exit(0);
    }
}
