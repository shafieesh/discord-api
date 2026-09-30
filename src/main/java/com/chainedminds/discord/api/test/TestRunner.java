package com.chainedminds.discord.api.test;

import com.chainedminds.utilities.json.Json;

public class TestRunner {

    public static  void main(String[] args) {

        TestDiscordAPI.config(2, "cred", "test", "en");

        TestDiscordData a = new TestDiscordData();
        a.guildID = 1L;

        System.out.println(Json.getString(a));
    }
}
