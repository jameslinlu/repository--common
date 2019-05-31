package com.commons.metadata.model.collect;

import org.apache.flume.Context;
import org.apache.flume.Event;

/**
 * Copyright (C)
 * IParse
 * Author: jameslinlu
 */
public interface IParse {
    Object parseEvent(Event event);

    public void configure(Context context);
}
