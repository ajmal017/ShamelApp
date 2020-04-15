package com.arabdevelopers.shamelapp.models;

import java.io.Serializable;
import java.util.List;

public class NotificationDataModel implements Serializable {

    private Data data;

    public Data getData() {
        return data;
    }

    public static class Data
    {
        private int current_page;
        private int last_page;
        private List<NotificationModel> data;

        public int getCurrent_page() {
            return current_page;
        }

        public int getLast_page() {
            return last_page;
        }

        public List<NotificationModel> getData() {
            return data;
        }

        public  class NotificationModel implements Serializable
        {
            private int id;
            private String from_user_id;
            private String to_user_id;
            private String title;
            private String message;
            private String is_read;
            private String status;
            private String notification_date;

            public int getId() {
                return id;
            }

            public String getFrom_user_id() {
                return from_user_id;
            }

            public String getTo_user_id() {
                return to_user_id;
            }

            public String getTitle() {
                return title;
            }

            public String getMessage() {
                return message;
            }

            public String getIs_read() {
                return is_read;
            }

            public String getStatus() {
                return status;
            }

            public String getNotification_date() {
                return notification_date;
            }
        }
    }


}
