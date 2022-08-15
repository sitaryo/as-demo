import 'package:flutter/material.dart';
import 'package:flutter_hooks/flutter_hooks.dart';
import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:oauth2/oauth2.dart' as oath2;
import 'package:public_client/service/oauth2_service.dart';

class Authorized extends HookWidget {
  const Authorized({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final client = useState<oath2.Client?>(null);
    final message = useState("");
    final sending = useState(false);

    useEffect(() {
      Oauth2Service.getClient(param: Uri.base.queryParameters).then((c) {
        client.value = c;
      });
    }, []);

    getMessage() {
      sending.value = true;
      print(
          "use access token ${client.value?.credentials.accessToken} to get message");
      // client.value
      //     ?.get(Uri.parse("http://resrouce.localhost:7070/message"))
      //     .then((data) {
      //   message.value = data.body;
      //   print("you get message:\n ${data.body}");
      // }).whenComplete(() => sending.value = false);

      print("here is your id token: ${client.value?.credentials.idToken}");
      final jwt = JwtDecoder.decode(client.value?.credentials.idToken ?? "");
      print("here is id token info :$jwt");
      client.value
          ?.get(Uri.parse("http://auth.localhost:8080/userinfo"))
          .then((data) {
        message.value = data.body;
        print("you get message:\n ${data.body}");
      }).whenComplete(() => sending.value = false);
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text("authorized"),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Text('Press button to get message'),
            if (sending.value)
              const SizedBox.square(
                dimension: 50,
                child: CircularProgressIndicator(),
              ),
            if (message.value != "") ...[
              const Text('you got message :'),
              Text(message.value)
            ],
            ElevatedButton(
              child: const Text("logout"),
              onPressed: () {
                Oauth2Service.logout();
                Navigator.pop(context);
              },
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: getMessage,
        tooltip: 'get message',
        child: const Icon(Icons.send),
      ),
    );
  }
}
