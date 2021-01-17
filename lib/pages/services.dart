import 'package:flutter/material.dart';
import 'package:route_flutter/pages/base.dart';

class Services extends Base {
  @override
  _ServicesState createState() => _ServicesState();
}

class _ServicesState extends State<Services> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Base.colors["colorPrimaryDark"],
      body: Container(
        child: Stack(
          children: [
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
    super.initState();
  }
}
