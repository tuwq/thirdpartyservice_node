package com.xcyy.util;

import org.springframework.beans.BeanUtils;

public final class DtoUtil {

    public static<T, S> T adapt(T dto, S model) {
        BeanUtils.copyProperties(model, dto);
        return dto;
    }
}
