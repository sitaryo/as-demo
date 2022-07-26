import 'package:flutter/material.dart';
import 'package:public_client/page/authorized.dart';
import 'package:public_client/page/login.dart';
import 'package:public_client/service/storage_service.dart';
import 'package:url_strategy/url_strategy.dart';

void main() {
  setPathUrlStrategy();
  StorageService.init();
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Oauth2 PKCE Demo',
      theme: ThemeData(primarySwatch: Colors.blue),
      onGenerateRoute: (setting) {
        if (setting.name?.startsWith("/authorized") ?? false) {
          return MaterialPageRoute(builder: (_) => const Authorized());
        }
      },
      home: const Login(),
    );
  }
}
