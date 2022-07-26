import 'package:flutter/material.dart';
import 'package:flutter_hooks/flutter_hooks.dart';
import 'package:oauth2/oauth2.dart' as oath2;
import 'package:public_client/service/oauth2_service.dart';

class Authorized extends HookWidget {
  const Authorized({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final client = useState<oath2.Client?>(null);

    useEffect(() {
      Oauth2Service.getClient(param: Uri.base.queryParameters).then((c) {
        client.value = c;
      });
    }, []);

    message() {
      print(
          "use access token ${client.value?.credentials.accessToken} to get message");
      client.value
          ?.get(Uri.parse("http://resrouce.localhost:7070/message"))
          .then((data) {
        print("you get message:\n ${data.body}");
      });
    }

    return Scaffold(
      appBar: AppBar(
        title: const Text("authorized"),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: const <Widget>[
            Text(
              'Press button to get message',
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: message,
        tooltip: 'get message',
        child: const Icon(Icons.send),
      ),
    );
  }
}
