package com.voiceassistant.translate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Translate implements Serializable {
        @SerializedName("text")
        @Expose
        public List<String> text;

    }

