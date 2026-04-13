package cn.com.mfish.workflow.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ApproveType {
    SINGLE("single", "任何人审批"),
    SEQUENTIAL("sequential", "顺序审批"),
    CONCURRENT("concurrent", "并行审批");

    @JsonValue
    private final String type;
    private final String comment;

    ApproveType(String type, String comment) {
        this.type = type;
        this.comment = comment;
    }

}
