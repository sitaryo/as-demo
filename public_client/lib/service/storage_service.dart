import 'package:shared_preferences/shared_preferences.dart';
import 'package:oauth2/oauth2.dart' as oauth2;

class StorageService {
  static late final SharedPreferences _store;
  static const _credentials = "credentials";
  static const _codeVerifier = "codeVerifier";

  static init() async {
    _store = await SharedPreferences.getInstance();
  }

  static clear() {
    _store.clear();
  }

  static setCredentials(oauth2.Credentials credentials) {
    _store.setString(_credentials, credentials.toJson());
  }

  static oauth2.Credentials? getCredentials() {
    final s = _store.getString(_credentials);
    if (s != null) {
      return oauth2.Credentials.fromJson(s);
    }
    return null;
  }

  static setCodeVerifier(String code) {
    _store.setString(_codeVerifier, code);
  }

  static String? getCodeVerifier() {
    return _store.getString(_codeVerifier);
  }
}
