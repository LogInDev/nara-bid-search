package com.nivuskorea.procurement.dto;

public enum DetailProduct {
    PROCESS_CONTROL("프로세스제어반", "4111249801"),
    FLOWMETER("유량계", "4111250101"),
    CONTROLLER("계장제어장치", "3912118901");

    private final String name;
    private final String detailNumber;

    DetailProduct(String name, String detailNumber) {
        this.name = name;
        this.detailNumber = detailNumber;
    }

    public String getName() {
        return name;
    }

    public String getDetailNumber() {
        return detailNumber;
    }

    // 이름 -> 품번으로
    public static String getByName(String name) {
        for (DetailProduct item : values()) {
            if (item.getName().equals(name)) {
                return item.getDetailNumber();
            }
        }
        throw new IllegalArgumentException("Unknown detail product: " + name);
    }
}
