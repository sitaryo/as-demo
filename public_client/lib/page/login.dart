import 'package:flutter/material.dart';
import 'package:flutter_hooks/flutter_hooks.dart';
import 'package:public_client/service/oauth2_service.dart';

class Login extends HookWidget {
  const Login({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {

    login(){
      Oauth2Service.login();
    }
    return Scaffold(
      appBar: AppBar(
        title: const Text("oauth login"),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: const <Widget>[
            Text(
              'Press button to login',
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: login,
        tooltip: 'login',
        child: const Icon(Icons.login_outlined),
      ),
    );
  }
}
