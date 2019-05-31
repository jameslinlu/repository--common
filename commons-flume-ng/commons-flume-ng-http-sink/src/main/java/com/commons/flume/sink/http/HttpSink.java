package com.commons.flume.sink.http;

import com.commons.common.utils.HttpClientHelper;
import com.commons.metadata.model.http.HttpResponse;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.flume.sink.AbstractSink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class HttpSink extends AbstractSink implements Configurable {

    private static final Logger logger = LoggerFactory.getLogger(HttpSink.class);


    public static final String SERIALIZER = "serializer";
    public static final String SERIALIZER_PREFIX = SERIALIZER + ".";


    private Context context;
    private SinkCounter counter;
    private HttpRequestFactory requestFactory;
    private String url;
    private Map<String, String> headers;
    private String method;


    @Override
    public Status process() throws EventDeliveryException {
        Channel channel = getChannel();
        Transaction transaction = null;


        try {

            transaction = channel.getTransaction();
            transaction.begin();

            Event event = channel.take();
            if (event == null) {
                transaction.commit();
                return Status.BACKOFF;
            }

            byte[] eventBody = event.getBody();


            if (logger.isDebugEnabled()) {
                logger.debug("{Event}  " + new String(eventBody, "UTF-8"));
            }

            HttpRequest request = requestFactory.createRequest(event);
            if (request == null) {
                transaction.commit();
                return Status.BACKOFF;
            }
            request.setUrl(this.url);
            request.setHeaders(this.headers);
            request.setMethod(this.method);

            HttpResponse response = null;
            if (request.getMethod().equalsIgnoreCase("post")) {
                if (request.getParams() != null) {
                    response = HttpClientHelper.getInstance().post(request.getUrl(), request.getParams(), request.getHeaders());
                }
                if (request.getEntity() != null) {
                    response = HttpClientHelper.getInstance().post(request.getUrl(), request.getEntity(), request.getHeaders());
                }
            }
            if (request.getMethod().equalsIgnoreCase("get")) {
                if (request.getParams() != null) {
                    response = HttpClientHelper.getInstance().get(request.getUrl(), request.getParams(), request.getHeaders());
                }
            }
            if (response.isError()) {
                transaction.commit();
                return Status.BACKOFF;
            }

            transaction.commit();
            counter.addToEventDrainSuccessCount(1);
            return Status.READY;
        } catch (Exception ex) {
            logger.error("Failed to Http Process", ex);
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception e) {
                    logger.error("Transaction rollback failed", e);
                    throw Throwables.propagate(e);
                }
            }
            return Status.BACKOFF;

        } finally {
            if (transaction != null) {
                transaction.close();
            }
        }

    }

    @Override
    public void configure(Context context) {

        this.context = context;

        url = Preconditions.checkNotNull(context.getString("httpUrl"), "http.url is required");
        logger.debug("Using Url: {}", url);
        method = context.getString("httpMethod", "post");
        logger.debug("Using Method: {}", url);

        String serializerClazz = Preconditions.checkNotNull(context.getString(SERIALIZER), "Request Serializer is Required");
        Context serializerContext = new Context();
        serializerContext.putAll(context.getSubProperties(SERIALIZER_PREFIX));

        try {
            @SuppressWarnings("unchecked")
            Class<? extends Configurable> clazz = (Class<? extends Configurable>) Class.forName(serializerClazz);
            Configurable serializer = clazz.newInstance();

            if (serializer instanceof HttpRequestFactory) {
                requestFactory = (HttpRequestFactory) serializer;
                requestFactory.configure(serializerContext);
            } else {
                throw new IllegalArgumentException(serializerClazz + " is not an HttpRequestFactory Serializer");
            }
        } catch (Exception e) {
            logger.error("Could not instantiate event serializer.", e);
            Throwables.propagate(e);
        }

        if (counter == null) {
            counter = new SinkCounter(getName());
        }
    }
}
