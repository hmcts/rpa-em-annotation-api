package uk.gov.hmcts.reform.em.annotation.service.util;

import org.apache.commons.collections.CollectionUtils;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class StringUtilities {

    private StringUtilities() {
    }

    public static String convertValidLogString(String log) {
        List<String> list = Arrays.asList("%0d", "\r", "%0a", "\n");

        // normalize the log content
        String encode = Normalizer.normalize(log, Normalizer.Form.NFKC);
        for (String toReplaceStr : list) {
            encode = encode.replace(toReplaceStr, "");
        }
        return encode;
    }

    public static UUID convertValidLogUUID(UUID log) {
        List<String> list = Arrays.asList("%0d", "\r", "%0a", "\n");

        // normalize the log content
        String encode = Normalizer.normalize(log.toString(), Normalizer.Form.NFKC);
        for (String toReplaceStr : list) {
            encode = encode.replace(toReplaceStr, "");
        }
        return UUID.fromString(encode);
    }

    public static List<String> convertValidLogString(List<String> logs) {
        if (CollectionUtils.isNotEmpty(logs)) {
            return logs.stream()
                .map(StringUtilities:: convertValidLogString)
                .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public static List<UUID> convertValidLogUUID(List<UUID> logs) {
        if (CollectionUtils.isNotEmpty(logs)) {
            return logs.stream()
                .map(StringUtilities:: convertValidLogUUID)
                .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }
}
