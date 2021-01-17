import 'package:flutter/material.dart';
import 'package:route_flutter/pages/base.dart';
import 'package:route_flutter/pages/services.dart';

class Splash extends Base {
  @override
  _SplashState createState() => _SplashState();
}

class _SplashState extends State<Splash> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Base.colors["colorPrimaryDark"],
      body: Container(
        child: Stack(
          children: [
            Center(
                child: Image.asset(
              "assets/images/logo.gif",
              height: 200,
            )),
            Positioned.fill(
                child: Image.asset(
              "assets/images/ss.png",
              alignment: Alignment.bottomRight,
            )),
          ],
        ),
      ),
    );
  }

  @override
  void initState() {
    Future.delayed(const Duration(seconds: 4), () {
      Navigator.pushReplacement(context,
          MaterialPageRoute(builder: (BuildContext context) => Services()));
    });
    super.initState();
  }
}
