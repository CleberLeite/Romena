package com.frf.app.utils;

public class CONSTANTS {

    public static final int OS_ID = 2;

    public static final String APP_PACKAGE_NAME                         = "com.sportm.romena";
    public static final String URL_SHARE                                = "www.frf.ro";

    public static final String SHARED_PREFS_KEY_FILE                    = APP_PACKAGE_NAME + ".APP_PREFS";
    public static final String SHARED_PREFS_SECRET_KEY                  = "prts04112016";

    public static String mainServer = "https://api.app-admin.frf.ro/";
    public static String mainServerShop = "https://api.contentspeed.ro/v1/";
    public static String mainServerTickets = "http://k2plqg2got.bilete.ro/api/";
    public static String access_token_shop = "Bearer nROMrJq0qmmw4F1ZRS8hPKmXGeriv43d5oaJFXtSAAZOPWl56vRyArRDbdPY1l8U";
    public static String access_token_tickets = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJBdXRoU2VydmVyQXBpIiwiaWF0IjoxNjM2NzI1NDQ1LCJleHAiOjE2NjgyNjE0NDUsImF1ZCI6ImZyZi5ybyIsInN1YiI6InNsaW1AZXhhbXBsZS5jb20iLCJFbWFpbCI6InNsaW1AYmlsZXRlLnJvIn0.uH_3ujuN6SJ7MJ1eYmJYBHV-4WA_-JbcpZg391zSZm4";
    public static String serverAddress = "https://api.app-admin.frf.ro/";
    public static String getVersion = mainServer + "api/";
    public static final String WS_API_HEADER_KEY = "X-Smapikey";
    public static String serverURL = serverAddress + "api/";
    public static String link_ecommerce = "";
    public static String link_ticketing = "";
    public static final String serverContentImages = serverAddress + "Content/images/";
    public static String serverVersion = "";

    public static final String SHARED_PREFS_KEY_APNS                           = "prts_apns";
    public static final String SHARED_PREFS_KEY_NEED_TO_UPDATE_APNS            = "prts_need_to_update_apns";
    public static final String SHARED_PREFS_KEY_GCM_TOKEN                      = "prts_gcm_token";
    public static final String SHARED_KEY_USER_ID                  = "prts_id_user";

    public static final String LOADING_SCREEN_KEY   = "screenKey";
    public static final int SCREEN_REDIRECT_PUSH    = 4;

    public static final int ERROR_CODE_MAINTENANCE      = 666;
    public static final int ERROR_CODE_UPDATE_SUGGESTED = 425;
    public static final int ERROR_CODE_UPDATE_REQUIRED  = 403;

    public static final int CONNECTION_TIMEOUT = 20 * 1000;


    public static final int CONNECTION_TYPE_NONE = 0;
    public static final int CONNECTION_TYPE_WIFI = 1;
    public static final int CONNECTION_TYPE_MOBILE = 2;

    public static final String WS_METHOD_VERSION_CHECK                      = "getVersion";

    //WS
    public static final String WS_METHOD_SIGN_IN                            = "signin";
    public static final String WS_METHOD_SIGN_IN_USER                       = "signin?idUser=%s";
    public static final String WS_METHOD_SIGN_UP                            = "signup";
    public static final String WS_METHOD_FORGOT_PASSWORD                    = "recoverPassword";
    public static final String SHARED_PREFS_KEY_ENDSESSION                  = "prts_endsession";
    public static final String WS_METHOD_SET_END_SESSION                    = "setEndSession";
    public static final String WS_METHOD_SAVE_TRACKING                      = "saveTracking";
    public static final String WS_METHOD_DELETE_ACCOUNT                     = "deleteAccount";
    public static final String WS_METHOD_GET_NOTIFICATION_HISTORY           = "getNotificationHistory?page=%s";
    public static final String WS_METHOD_GET_NOTIFICATION_MARK_ALL          = "setAllRead";
    public static final String WS_METHOD_GET_BADGES                         = "getBadges";
    public static final String WS_METHOD_GET_BADGES_PAGE                    = "getBadges?page=%s";
    public static final String WS_METHOD_GET_BADGES_DETAILS                 = "getBadgeDetails";
    public static final String WS_METHOD_SET_USER_APNS                      = "setUserApns";
    public static final String WS_METHOD_GET_USER_PROFILE                   = "getUserProfile";
    public static final String WS_METHOD_UPDATE_USER                        = "updateUserProfile";
    public static final String WS_METHOD_UPLOAD_PICTURE                     = "uploadAvatar";
    public static final String WS_METHOD_SET_NOTIFICATION                   = "setNotificationRead?idPush=%s";
    public static final String WS_METHOD_GET_ALL_NEWS                       = "getAllNews";
    public static final String WS_METHOD_GET_HOME                           = "getHome";
    public static final String WS_METHOD_GET_ALL_VIDEOS                     = "getAllVideos";
    public static final String WS_METHOD_GET_POST_ARENA                     = "getArenaPost";
    public static final String WS_METHOD_GET_ARENA_FEED                     = "getArenaFeed";
    public static final String WS_METHOD_GET_PRIZES                         = "getPrizes";
    public static final String WS_METHOD_GET_PRIZES_DETAILS                 = "getPrizeDetails?id=";
    public static final String WS_METHOD_SET_ARENA_POST_LIKE                = "setArenaPostLike";
    public static final String WS_METHOD_SET_ARENA_POST_DELETE              = "deleteArenaPost";
    public static final String WS_METHOD_SET_ARENA_POST_DELETE_COMENTARY    = "deleteArenaPostComment";
    public static final String WS_METHOD_SET_ARENA_POST                     = "setArenaPost";
    public static final String WS_METHOD_GET_NEWS_CATEGORIES                = "getNewsCategories";
    public static final String WS_METHOD_GET_ARENA_CATEGORIES               = "getArenaCategories";
    public static final String WS_METHOD_GET_NEWS                           = "getNews";
    public static final String WS_METHOD_GET_NOTIFICATIONS_PERMISSION       = "getNotificationsPermissions";
    public static final String WS_METHOD_GET_NOTIFICATIONS_PERMISSION_READ  = "setNotificationsPermissions";
    public static final String WS_METHOD_GET_NEWS_FAVORITE                  = "setNewsFavorite";
    public static final String WS_METHOD_GET_QUIZ_ANSWER                    = "setQuizAnswer";
    public static final String WS_METHOD_SET_ARENA_COMMENT                  = "setArenaComment";
    public static final String WS_METHOD_GET_QUIZ                           = "getQuizzes?page=";
    public static final String WS_METHOD_GET_COMPETITION_TABLES             = "getCompetitionTables?idCompetition=";
    public static final String WS_METHOD_GET_ARENA_POST_REPLIES             = "getArenaPostReplies";
    public static final String WS_METHOD_GET_COMPETITION_MATCHES            = "getCompetitionMatches";
    public static final String WS_METHOD_GET_COMPETITION_MATCHE             = "getCompetitionMatch";
    public static final String WS_METHOD_GET_PROFILE_COINS                  = "getEditProfileCoins";
    public static final String WS_METHOD_SET_FOLLOW_VIDEOS                  = "setFollowVideosCategory";
    public static final String WS_METHOD_SET_FOLLOW_NEWS                    = "setFollowNewsCategory";
    public static final String WS_METHOD_SET_NOTIFICATION_MATCH             = "setNotificationMatch";
    public static final String WS_METHOD_GET_NEWS_COMMENT                   = "getNewsComment?id=";
    public static final String WS_METHOD_DELETE_NEWS_COMMENT                = "deleteNewsComment";
    public static final String WS_METHOD_SET_NEWS_COMMENT                   = "setNewsComment";
    public static final String WS_METHOD_GET_NEWS_COMMENT_REPLIES           = "getNewsCommentReplies";
    public static final String WS_METHOD_SET_RESCUE                         = "setPrizeRedeem";
    public static final String WS_METHOD_SET_AUTH                           = "auth";
    public static final String WS_METHOD_GET_REPOSTS                        = "getReportTypes";
    public static final String WS_METHOD_SET_REPOST                         = "setReport";
    public static final String WS_METHOD_SET_BLOCK_USER                     = "setBlockUser";
    //Ranking
    public static final String WS_METHOD_GET_RANK                           = "getRanking";
    public static final String WS_METHOD_GET_RANK_TITLE                     = "getRankingTiers";
    public static final String WS_METHOD_GET_MATCH_COMPETITIONS             = "getCompetitions";

    //Shop
    public static final String WS_METHOD_GET_HOMEPAGE                       = "utilities/homepage";
    public static final String WS_METHOD_GET_CATEGORIES                     = "categories";
    public static final String WS_METHOD_GET_SHOP_PRODUCTS                  = "products";
    public static final String WS_METHOD_GET_SEARCH_SHOP                    = "search/";

    //Tickets
    public static final String WS_METHOD_GET_COMPETITIONS                   = "competitions";
    public static final String WS_METHOD_GET_MATCHES                        = "matches?CompetitionId=%s";
    public static final String WS_METHOD_GET_SECTORS                        = "events/%s/map/sectors";
    public static final String WS_METHOD_GET_SECTORS_SVG                    = "events/%s/map/sectors/svg";
    public static final String WS_METHOD_GET_OFFERS                         = "offers/%s";


    //End session
    public static final int END_SESSION_TYPE_ANDROID = 1;
    public static final int END_SESSION_TYPE_ANDROID_BACKGROUND = 2;
    public static final int END_SESSION_TYPE_ADNROID_KILL_APP = 3;

    public static final int SCREEN_SEARCH           = 3;


}
