package com.facepp.library.bean;

import java.util.List;

/**
 * Created by zwj on 2017/9/22.
 */

public class SearchFaceData {

    /**
     * image_id : Cqr65TrK2/b10u97Ecmc2g==
     * faces : [{"face_rectangle":{"width":285,"top":49,"left":97,"height":285},"face_token":"a21a530e3a2be2f9857f57d4b237ad21"}]
     * time_used : 451
     * thresholds : {"1e-3":62.327,"1e-5":73.975,"1e-4":69.101}
     * request_id : 1506043739,7a199251-471a-4d39-aada-f9560563fd06
     * results : [{"confidence":50.766,"user_id":"","face_token":"94fda6ef0fa3fcd8d0ee901affe4cac7"}]
     */

    private String image_id;
    private int time_used;
  //  private ThresholdsBean thresholds;
    private String request_id;
    private List<FacesBean> faces;
    private List<ResultsBean> results;

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public int getTime_used() {
        return time_used;
    }

    public void setTime_used(int time_used) {
        this.time_used = time_used;
    }


    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public List<FacesBean> getFaces() {
        return faces;
    }

    public void setFaces(List<FacesBean> faces) {
        this.faces = faces;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }


    public static class FacesBean {
        /**
         * face_rectangle : {"width":285,"top":49,"left":97,"height":285}
         * face_token : a21a530e3a2be2f9857f57d4b237ad21
         */

        private FaceRectangleBean face_rectangle;
        private String face_token;

        public FaceRectangleBean getFace_rectangle() {
            return face_rectangle;
        }

        public void setFace_rectangle(FaceRectangleBean face_rectangle) {
            this.face_rectangle = face_rectangle;
        }

        public String getFace_token() {
            return face_token;
        }

        public void setFace_token(String face_token) {
            this.face_token = face_token;
        }

        public static class FaceRectangleBean {
            /**
             * width : 285
             * top : 49
             * left : 97
             * height : 285
             */

            private int width;
            private int top;
            private int left;
            private int height;

            public int getWidth() {
                return width;
            }

            public void setWidth(int width) {
                this.width = width;
            }

            public int getTop() {
                return top;
            }

            public void setTop(int top) {
                this.top = top;
            }

            public int getLeft() {
                return left;
            }

            public void setLeft(int left) {
                this.left = left;
            }

            public int getHeight() {
                return height;
            }

            public void setHeight(int height) {
                this.height = height;
            }
        }
    }

    public static class ResultsBean {
        /**
         * confidence : 50.766
         * user_id :
         * face_token : 94fda6ef0fa3fcd8d0ee901affe4cac7
         */

        private double confidence;
        private String user_id;
        private String face_token;

        public double getConfidence() {
            return confidence;
        }

        public void setConfidence(double confidence) {
            this.confidence = confidence;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getFace_token() {
            return face_token;
        }

        public void setFace_token(String face_token) {
            this.face_token = face_token;
        }
    }
}
