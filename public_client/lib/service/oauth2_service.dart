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
  static final redirectUrl = Uri.parse('http://127.0.0.1:8989/authorized');

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
    var authorizationUrl = _grant(code).getAuthorizationUrl(
      redirectUrl,
      scopes: ["read", "write"],
    );
    await _redirect(authorizationUrl);
  }

  static _redirect(Uri uri) {
    if (kIsWeb) {
      html.window.open(uri.toString(), "_self");
    } else {
      // todo android and iOS
    }
  }

  static Future<oauth2.Client> getClient({Map<String, String>? param}) async {
    final credentials = StorageService.getCredentials();
    final code = StorageService.getCodeVerifier();

    if (code == null) {
      throw Exception("please login first");
    }

    if (credentials != null) {
      return oauth2.Client(credentials, identifier: identifier);
    }
    if (param != null) {
      final grant = _grant(code);
      grant.getAuthorizationUrl(
        redirectUrl,
        scopes: ["read", "write"],
      );
      final client = await grant.handleAuthorizationResponse(param);
      StorageService.setCredentials(client.credentials);
      return client;
    }
    throw Exception("please provide AuthorizationCode");
  }
}
