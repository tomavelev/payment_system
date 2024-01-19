package com.tomavelev.payment.model.response;

import java.util.List;

public record RestResponse<T>(List<T> list, long count, String message, BusinessCode code) {
}
