package com.chainedminds.discord.api.models;

import com.chainedminds.models._FileData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class _DiscordData {

    public final AccountData account = new AccountData();
    public final ClientData client = new ClientData();

    public int request;
    public Integer subRequest;
    public int response;

    public String message;
    public List<String> messages;

    public _FileData file;
    public List<_FileData> files;

    public Long guildID;
    public Long categoryID;
    public Long channelID;

    public String channelName;

    public Guild guild;

    public Collection<String> channelNames;
    public Map<String, Long> channelNameIDs;
    public Map<Long, String> channelIDNames;
    public Map<String, List<String>> channelNameMessages;

    public Map<Long, String> roleIDNames;

    public Long userID;
    public Collection<Long> userIDs;

    public User user;
    public List<User> users;

    public CompactEmbed embed;

    public static class AccountData {

        public static int id;
        public static String credential;
    }

    public static class ClientData {

        public static String appName = "API";
        public final String platform = "API";
        public final String version = "1.0.0";
        public static String language = "en";
    }

    public static class CompactEmbed {

        public String title;
        public String description;
        public Integer color;
        public List<Field> fields = new ArrayList<>();
        public String footer;
        public List<SelectionData> selections = new ArrayList<>();
        public List<Long> mentionUsers;
        public List<Long> mentionRoles;

        public static class Field {

            public String name = "\u200E";
            public String value = "\u200E";
            boolean inline = false;

            public Field() {

            }

            public Field(String name, String value) {

                this.name = name;
                this.value = value;
            }

            public Field(String name, String value, boolean inline) {

                this.name = name;
                this.value = value;
                this.inline = inline;
            }

            public Field(boolean inline) {

                this.inline = inline;
            }
        }

        public static class SelectionData {

            public String id;
            public String placeHolder;
            public List<OptionData> options = new ArrayList<>();

            public static SelectionData of(String id, OptionData... options) {

                SelectionData selection = new SelectionData();
                selection.id = id;

                if (options != null) {

                    selection.options = List.of(options);
                }

                return selection;
            }

            public static SelectionData of(String id) {

                SelectionData selection = new SelectionData();
                selection.id = id;

                return selection;
            }

            public void setPlaceHolder(String placeHolder) {

                this.placeHolder = placeHolder;
            }

            public void addOption(OptionData option) {

                this.options.add(option);
            }


            public static class OptionData {

                public String key;
                public String title;
                public String description;
                public String emoji;

                public OptionData() {

                }

                public OptionData(String key, String title) {

                    this.key = key;
                    this.title = title;
                }

                public OptionData(String key, String title, String description) {

                    this.key = key;
                    this.title = title;
                    this.description = description;
                }
            }

            public static class ButtonData {

                public String type;
                public String id;
                public String url;
                public String label;

                public static ButtonData link(String url, String label) {

                    ButtonData button = new ButtonData();
                    button.type = "link";
                    button.url = url;
                    button.label = label;

                    return button;
                }

                public static ButtonData primary(String id, String label) {

                    ButtonData button = new ButtonData();
                    button.type = "primary";
                    button.id = id;
                    button.label = label;

                    return button;
                }

                public static ButtonData secondary(String id, String label) {

                    ButtonData button = new ButtonData();
                    button.type = "secondary";
                    button.id = id;
                    button.label = label;

                    return button;
                }

                public static ButtonData success(String id, String label) {

                    ButtonData button = new ButtonData();
                    button.type = "success";
                    button.id = id;
                    button.label = label;

                    return button;
                }

            }
        }
    }

    public static class Guild {

        public Long id;

        public User member;
        public List<User> members;

        public Long memberID;
        public List<Long> memberIDs;
    }

    public static class User {

        public Long id;
        public String username;
        public String displayName;
        public String avatar;
        public String guildTag;
    }
}