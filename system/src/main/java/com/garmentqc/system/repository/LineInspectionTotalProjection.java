package com.garmentqc.system.repository;

public interface LineInspectionTotalProjection {

    String getLineId();

    String getLineName();

    Long getTotalInspections();

    Long getActualQuantity();
}