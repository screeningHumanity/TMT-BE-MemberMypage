package ScreeningHumanity.MemberMypageServer.bookmark.vo.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class BookmarkOutVo {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IsBookmark {

        private Boolean isBookmark;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class registeredBookmark {

        @JsonProperty(value = "id")
        private Long indexId;

        private String stockName;

        private String stockCode;

        private Long price;

        @JsonProperty(value = "prdy_ctrt")
        private String prdyCtrt; //등락률
    }
}
