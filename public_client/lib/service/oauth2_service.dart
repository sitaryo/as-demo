import 'dart:html' as html;

import 'package:flutter/foundation.dart';
import 'package:oauth2/oauth2.dart' as oauth2;
import 'package:public_client/service/storage_service.dart';
import 'package:uuid/uuid.dart';

class Oauth2Service {
  static final authorizationEndpoint =
      Uri.parse('http://auth.localhost:8080/oauth2/authorize');
  static final tokenEndpoint =
      Uri.parse('http://auth.localhost:8080/oauth2/token');
  static const identifier = 'licky-public';
  static final redirectUrl = Uri.parse('http://192.168.3.137:8989/authorized');

  static oauth2.AuthorizationCodeGrant _grant(String code) {
    return oauth2.AuthorizationCodeGrant(
      identifier,
      authorizationEndpoint,
      tokenEndpoint,
      codeVerifier: code,
    );
  }

  static login() async {
    final code = const Uuid().v4();
    StorageService.setCodeVerifier(code);
    var authorizationUrl = _grant(code).getAuthorizationUrl(redirectUrl,
        scopes: ["openid", "email", "read", "write"]);
    await _redirect(authorizationUrl);
  }

  static _redirect(Uri uri) {
    if (kIsWeb) {
      html.window.open(uri.toString(), "_self");
    } else {
      // todo android and iOS
    }
  }

  static Future<oauth2.Client> fetchTokenWithParam(
      Map<String, String>? param) async {
    final code = StorageService.getCodeVerifier();
    if (code == null) {
      throw Exception("please login first");
    }
    if (param != null && param.isNotEmpty) {
      final grant = _grant(code);
      grant.getAuthorizationUrl(redirectUrl,
          scopes: ["openid", "email", "read", "write"]);
      final client = await grant.handleAuthorizationResponse(param);
      StorageService.setCredentials(client.credentials);
      return client;
    }
    throw Exception("param [$param] or code [$code] should not be null");
  }

  static Future<oauth2.Client> getClient({Map<String, String>? param}) async {
    final credentials = StorageService.getCredentials();
    if (credentials != null) {
      final client = oauth2.Client(credentials, identifier: identifier);
      // client not expired
      if (!client.credentials.isExpired) {
        return client;
      }

      // try refresh token
      try {
        await client.refreshCredentials();
        return client;
      } catch (e) {
        // refresh failed, get token again if param has value
        return fetchTokenWithParam(param);
      }
    }

    // credentials not exist in storage
    return fetchTokenWithParam(param);
  }

  static void logout() => StorageService.clear();
}
