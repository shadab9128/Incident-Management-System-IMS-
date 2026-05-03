package com.ims.backend.mapper;

import com.ims.backend.dto.SignalRequest;
import com.ims.backend.model.Signal;

public class SignalMapper {

    public static Signal toModel(SignalRequest request) {
        Signal signal = new Signal();
        signal.setComponentId(request.getComponentId());
        signal.setSeverity(request.getSeverity());
        signal.setMessage(request.getMessage());
        signal.setTimestamp(request.getTimestamp());
        return signal;
    }
}