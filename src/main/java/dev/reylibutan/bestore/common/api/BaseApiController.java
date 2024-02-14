package dev.reylibutan.bestore.common.api;

public class BaseApiController {

  private static final String API_PREFIX_ADMIN = "/admin";
  private static final String API_PATH = "/api";
  private static final String BASE_API_VERSION = "/v1";

  public static final String BASE_API_PATH = API_PATH + BASE_API_VERSION;
  public static final String BASE_API_PATH_ADMIN = API_PREFIX_ADMIN + BASE_API_PATH;
}
