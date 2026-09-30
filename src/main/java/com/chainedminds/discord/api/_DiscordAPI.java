package com.chainedminds.discord.api;

import com.chainedminds._Codes;
import com.chainedminds.api._API;
import com.chainedminds.discord.api.models._DiscordData;
import com.chainedminds.models._FileData;
import com.chainedminds.utilities.SocketPool;
import com.chainedminds.utilities.json.Json;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class _DiscordAPI extends _API {

    public static final Map<String, String> EMBED_STORAGE = new HashMap<>();

    public static _DiscordAPI INSTANCE;
    public static final SocketPool POOL = new SocketPool("engine-discord.chainedminds.com", 4395, 5);
    public static final ExecutorService ASYNC_POOL_EXECUTOR = Executors.newCachedThreadPool();

    public static synchronized _DiscordAPI get() {

        if (INSTANCE == null) {

            INSTANCE = new _DiscordAPI();
        }

        return INSTANCE;
    }
    
    public static void config(int id, String credential, String appName, String language) {
        
        _DiscordData.AccountData.id = id;
        _DiscordData.AccountData.credential = credential;
        _DiscordData.ClientData.appName = appName;
        _DiscordData.ClientData.language = language;
    }

    public static boolean canUpdateEmbed(_DiscordData.CompactEmbed embed) {

        String key = embed.footer;
        String jsonEmbed = Json.getString(embed);

        if (!jsonEmbed.equals(EMBED_STORAGE.get(key))) {

            EMBED_STORAGE.put(key, jsonEmbed);

            return true;
        }

        return false;
    }

    public void call(_DiscordData request, boolean async, ApiCallback callback) {

        callPool(request, async, callback);
    }

    public void callHttp(_DiscordData request, boolean async, ApiCallback callback) {

        String requestJson = Json.getString(request);

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");

        RequestBody body = RequestBody.create(requestJson, mediaType);

        Request.Builder builder = new Request.Builder();
        builder.url("https://api-discord.chainedminds.com/v2/");
        builder.post(body);

        call(builder, async, callback);
    }

    public void callPool(_DiscordData request, boolean async, ApiCallback callback) {

        if (async) {

            ASYNC_POOL_EXECUTOR.execute(() -> {

                byte[] requestBytes = Json.getBytes(request);

                byte[] responseBytes = SocketPool.transfer(POOL, requestBytes);

                if (callback != null) {

                    if (responseBytes != null) {

                        String responseString = new String(responseBytes);

                        callback.onResponse(200, responseString);
                        callback.onResponse(200, (Map<String, List<String>>) null, responseString);
                        callback.onResponse(200, (Headers) null, responseString);

                    } else {

                        callback.onError(new RuntimeException("Unknown error"));
                        callback.onError("Unknown error", "Unknown error");
                        callback.onError("Unknown error", new RuntimeException("Unknown error"));
                    }
                }
            });

        } else {

            byte[] requestBytes = Json.getBytes(request);

            byte[] responseBytes = SocketPool.transfer(POOL, requestBytes);

            if (callback != null) {

                if (responseBytes != null) {

                    String responseString = new String(responseBytes);

                    callback.onResponse(200, responseString);
                    callback.onResponse(200, (Map<String, List<String>>) null, responseString);
                    callback.onResponse(200, (Headers) null, responseString);

                } else {

                    callback.onError(new RuntimeException("Unknown error"));
                    callback.onError("Unknown error", "Unknown error");
                    callback.onError("Unknown error", new RuntimeException("Unknown error"));
                }
            }
        }
    }

    public boolean upload(File file) {

        AtomicBoolean wasSuccessful = new AtomicBoolean(false);

        RequestBody fileBody = RequestBody.create(file, MediaType.parse("application/octet-stream"));

        Request.Builder builder = new Request.Builder();
        builder.url("https://upload-discord.chainedminds.com/" + file.getName());
        builder.post(fileBody);

        _API.instance().call(builder, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI upload " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, Map<String, List<String>> headers, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    wasSuccessful.set(true);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return wasSuccessful.get();
    }

    //----------------------------------------------------------

    public boolean setEmbed(long guildID, long channelID, _DiscordData.CompactEmbed newEmbed) {

        AtomicBoolean wasSuccessful = new AtomicBoolean(false);

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2000;
        requestData.guildID = guildID;
        requestData.channelID = channelID;
        requestData.embed = newEmbed;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI setEmbed " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    wasSuccessful.set(true);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return wasSuccessful.get();
    }

    public boolean sendEmbed(long guildID, long channelID, _DiscordData.CompactEmbed newEmbed) {

        AtomicBoolean wasSuccessful = new AtomicBoolean(false);

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2003;
        requestData.guildID = guildID;
        requestData.channelID = channelID;
        requestData.embed = newEmbed;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI sendEmbed " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    wasSuccessful.set(true);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return wasSuccessful.get();
    }

    public boolean editEmbed(long guildID, long channelID, _DiscordData.CompactEmbed newEmbed) {

        AtomicBoolean wasSuccessful = new AtomicBoolean(false);

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2018;
        requestData.guildID = guildID;
        requestData.channelID = channelID;
        requestData.embed = newEmbed;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI editEmbed " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    wasSuccessful.set(true);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return wasSuccessful.get();
    }

    public boolean deleteEmbed(long guildID, long channelID, String embedID) {

        AtomicBoolean wasSuccessful = new AtomicBoolean(false);

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2001;
        requestData.guildID = guildID;
        requestData.channelID = channelID;
        requestData.embed = new _DiscordData.CompactEmbed();
        requestData.embed.footer = embedID;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI deleteEmbed " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    wasSuccessful.set(true);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return wasSuccessful.get();
    }

    //----------------------------------------------------------

    public boolean sendChannelMessage(long guildID, long channelID, String message) {

        return sendChannelMessage(guildID, channelID, List.of(message), null);
    }

    public boolean sendChannelMessage(long guildID, long channelID, String message, Path path) {

        return sendChannelMessage(guildID, channelID, List.of(message), List.of(path));
    }

    public boolean sendChannelMessage(long guildID, long channelID, String message, List<Path> paths) {

        return sendChannelMessage(guildID, channelID, List.of(message), paths);
    }

    public boolean sendChannelMessage(long guildID, long channelID, List<String> messages, List<Path> paths) {

        List<_FileData> files = null;

        if (paths != null) {

            files = new ArrayList<>();

            for (Path path : paths) {

                File file = path.toFile();

                if (upload(file)) {

                    _FileData fileData = new _FileData();
                    fileData.name = file.getName();

                    files.add(fileData);

                } else {

                    return false;
                }
            }
        }

        AtomicBoolean wasSuccessful = new AtomicBoolean(false);

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2002;
        requestData.guildID = guildID;
        requestData.channelID = channelID;
        requestData.messages = messages;
        requestData.files = files;

        call(requestData, true, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI sendMessage " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    wasSuccessful.set(true);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return wasSuccessful.get();
    }

    public boolean sendChannelMessageIfNotExists(long guildID, long channelID, String message) {

        AtomicBoolean wasSuccessful = new AtomicBoolean(false);

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2004;
        requestData.guildID = guildID;
        requestData.channelID = channelID;
        requestData.message = message;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI sendMessageIfNotExists " + error + " : " + message);
                System.out.println(Json.getString(requestData));
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    wasSuccessful.set(true);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return wasSuccessful.get();
    }

    public boolean sendChannelMessagesIfNotExists(long guildID, long channelID, List<String> newMessages) {

        AtomicBoolean wasSuccessful = new AtomicBoolean(false);

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2005;
        requestData.guildID = guildID;
        requestData.channelID = channelID;
        requestData.messages = newMessages;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI sendMessagesIfNotExists " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    wasSuccessful.set(true);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return wasSuccessful.get();
    }

    public boolean setChannelMessages(long guildID, long channelID, List<String> messages) {

        AtomicBoolean wasSuccessful = new AtomicBoolean(false);

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2010;
        requestData.guildID = guildID;
        requestData.channelID = channelID;
        requestData.messages = messages;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI setMessages " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    wasSuccessful.set(true);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return wasSuccessful.get();
    }

    public boolean sendUserMessage(long userID, String message) {

        return sendUserMessage(userID, List.of(message), null);
    }

    public boolean sendUserMessage(long userID, String message, Path path) {

        return sendUserMessage(userID, List.of(message), List.of(path));
    }

    public boolean sendUserMessage(long userID, List<String> messages, List<Path> paths) {

        List<_FileData> files = null;

        if (paths != null) {

            files = new ArrayList<>();

            for (Path path : paths) {

                File file = path.toFile();

                if (upload(file)) {

                    _FileData fileData = new _FileData();
                    fileData.name = file.getName();

                    files.add(fileData);

                } else {

                    return false;
                }
            }
        }

        AtomicBoolean wasSuccessful = new AtomicBoolean(false);

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2002;
        requestData.userID = userID;
        requestData.messages = messages;
        requestData.files = files;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI sendMessage " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    wasSuccessful.set(true);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return wasSuccessful.get();
    }

    //----------------------------------------------------------

    public Long addChannel(long categoryID, String channelName) {

        AtomicLong channelID = new AtomicLong();

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2006;
        requestData.categoryID = categoryID;
        requestData.channelName = channelName;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI addChannel " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    channelID.set(responseData.channelID);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return channelID.get();
    }

    public Map<String, Long> addChannels(long categoryID, Collection<String> channelNames) {

        AtomicReference<Map<String, Long>> channelIDs = new AtomicReference<>();

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2007;
        requestData.categoryID = categoryID;
        requestData.channelNames = channelNames;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI addChannels " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    channelIDs.set(responseData.channelNameIDs);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return channelIDs.get();
    }

    public Map<String, Long> setChannels(long categoryID, Collection<String> channelNames) {

        AtomicReference<Map<String, Long>> channelIDs = new AtomicReference<>();

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2008;
        requestData.categoryID = categoryID;
        requestData.channelNames = channelNames;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI setChannels " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    channelIDs.set(responseData.channelNameIDs);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return channelIDs.get();
    }

    public Map<String, Long> setChannelsAndSetMessages(long categoryID, Map<String, List<String>> channelNameMessages) {

        AtomicReference<Map<String, Long>> channelIDs = new AtomicReference<>();

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2009;
        requestData.categoryID = categoryID;
        requestData.channelNameMessages = channelNameMessages;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI setChannelsAndSetMessages " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    channelIDs.set(responseData.channelNameIDs);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return channelIDs.get();
    }

    public boolean setChannelName(long channelID, String channelName) {

        AtomicBoolean wasSuccessful = new AtomicBoolean(false);

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2011;
        requestData.channelID = channelID;
        requestData.channelName = channelName;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI setChannelName " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    wasSuccessful.set(true);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return wasSuccessful.get();
    }

    //----------------------------------------------------------

    public Map<Long, String> getCategoryChannels(long guildID, long categoryID) {

        AtomicReference<Map<Long, String>> channelIDs = new AtomicReference<>();

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2017;
        requestData.guildID = guildID;
        requestData.categoryID = categoryID;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI getCategoryChannels " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    channelIDs.set(responseData.channelIDNames);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return channelIDs.get();
    }

    //----------------------------------------------------------

    public List<Long> getGuildMemberIDs(long guildID) {

        AtomicReference<List<Long>> memberIDs = new AtomicReference<>();

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2012;
        requestData.guild = new _DiscordData.Guild();
        requestData.guild.id = guildID;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI getGuildMemberIDs " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    memberIDs.set(responseData.guild.memberIDs);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return memberIDs.get();
    }

    public List<_DiscordData.User> getUsers(Collection<Long> userIDs) {

        AtomicReference<List<_DiscordData.User>> memberIDs = new AtomicReference<>();

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2013;
        requestData.userIDs = userIDs;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI getUsers " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    memberIDs.set(responseData.users);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return memberIDs.get();
    }

    public boolean callbackGetUsers(Collection<Long> userIDs) {

        AtomicBoolean wasSuccessful = new AtomicBoolean(false);

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2014;
        requestData.userIDs = userIDs;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI callbackUsers " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    wasSuccessful.set(true);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return wasSuccessful.get();
    }

    public Map<Long, String> getRoles(long guildID) {

        AtomicReference<Map<Long, String>> roles = new AtomicReference<>();

        _DiscordData requestData = new _DiscordData();
        requestData.request = 1009;
        requestData.subRequest = 2019;
        requestData.guildID = guildID;

        call(requestData, false, new ApiCallback() {
            @Override
            public void onError(String error, String message) {

                System.err.println("DiscordAPI getRoles " + error + " : " + message);
            }

            @Override
            public void onResponse(int code, String response) {

                if (code != 200) {

                    throw new RuntimeException("Response code : " + code);
                }

                _DiscordData responseData = Json.getObject(response, _DiscordData.class);

                if (responseData != null && responseData.response == _Codes.RESPONSE_OK) {

                    roles.set(responseData.roleIDNames);

                } else {

                    throw new RuntimeException("Response  : " + response);
                }
            }
        });

        return roles.get();
    }

    //----------------------------------------------------------
}
