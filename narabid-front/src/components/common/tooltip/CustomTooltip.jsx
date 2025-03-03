import React from "react";
import styles from "./CustomTooltip.module.scss"; // 스타일 파일 따로 만들기

const CustomTooltip = (params) => {
    return (
        <div className={styles.tooltip}>
            {params.value}
        </div>
    );
};

export default CustomTooltip;
