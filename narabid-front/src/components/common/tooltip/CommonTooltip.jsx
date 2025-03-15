import React, { useState } from 'react'
import styles from './CommonTooltip.module.scss'

const CommonTooltip = ({ text, children }) => {
    const [isVisible, setIsVisible] = useState(false);

    return (
        <div
            className={styles.tooltipContainer}
            onMouseEnter={() => setIsVisible(true)}
            onMouseLeave={() => setIsVisible(false)}
        >
            {children} {/* 버튼이 들어갈 자리 */}
            {isVisible && <div className={styles.tooltip}>{text}</div>}
        </div>
    );
};

export default CommonTooltip