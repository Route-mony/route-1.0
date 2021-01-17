import 'package:flutter/material.dart';
import 'package:route_flutter/pages/services.dart';
import 'package:route_flutter/pages/splash.dart';

void main() {
  runApp(MaterialApp(
    initialRoute: '/',
    routes: {
      '/': (context) => Splash(),
      '/services': (context) => Services(),
    },
  ));
}
