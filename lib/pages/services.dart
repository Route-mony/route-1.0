import 'package:carousel_slider/carousel_slider.dart';
import 'package:flutter/material.dart';
import 'package:route_flutter/api/services.dart';
import 'package:route_flutter/models/adverts/advert.dart';
import 'package:route_flutter/pages/base.dart';

class Services extends Base {
  @override
  _ServicesState createState() => _ServicesState();
}

class _ServicesState extends State<Services> {
  ApiServices apiServices = ApiServices();
  List<Advert> adverts = List();

  @override
  Widget build(BuildContext context) {
    fetchServices();
    return Scaffold(
        backgroundColor: Base.colors["colorPrimaryDark"],
        body: Container(
            decoration: BoxDecoration(
                image: DecorationImage(
                    image: AssetImage("assets/images/ss.png"),
                    alignment: Alignment.bottomRight)),
            child: Column(mainAxisAlignment: MainAxisAlignment.end, children: [
              Padding(
                padding:
                    const EdgeInsets.symmetric(vertical: 0.0, horizontal: 10),
                child: CarouselSlider(
                  options: CarouselOptions(
                    autoPlay: true,
                    initialPage: 0,
                    height: MediaQuery.of(context).size.height * 0.8,
                    enableInfiniteScroll: true,
                    autoPlayInterval: Duration(seconds: 5),
                    scrollDirection: Axis.horizontal,
                    viewportFraction: 1,
                  ),
                  items: adverts.map((advert) {
                    return Padding(
                        padding: const EdgeInsets.all(20.0),
                        child: Container(
                            decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(20),
                                gradient: LinearGradient(
                                    colors: [
                                      Base.colors["cardStartColor"],
                                      Base.colors["cardEndColor"]
                                    ],
                                    begin: Alignment(-1.0, 0.0),
                                    end: Alignment(1.0, 0.0),
                                    transform: GradientRotation(45))),
                            child: Padding(
                              padding: const EdgeInsets.all(20.0),
                              child: Column(
                                mainAxisAlignment: MainAxisAlignment.center,
                                crossAxisAlignment: CrossAxisAlignment.center,
                                children: [
                                  Image(
                                    image: NetworkImage(advert.url),
                                    height: 300,
                                  ),
                                  Text(
                                    advert.title,
                                    style: TextStyle(
                                        color: Base.colors["colorAccent"],
                                        fontWeight: FontWeight.bold,
                                        fontSize: 18),
                                  ),
                                  SizedBox(
                                    height: 35,
                                  ),
                                  Text(
                                    advert.description,
                                    style: TextStyle(
                                      color: Base.colors["colorAccent"],
                                    ),
                                  )
                                ],
                              ),
                            )));
                  }).toList(),
                ),
              ),
              Padding(
                padding: const EdgeInsets.all(20.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  mainAxisSize: MainAxisSize.max,
                  children: [
                    MaterialButton(
                      onPressed: () {},
                      splashColor: Colors.transparent,
                      elevation: 0,
                      child: Text("SignUp",
                          style: TextStyle(
                              color: Base.colors["colorAccent"],
                              backgroundColor: Colors.transparent)),
                    ),
                    MaterialButton(
                      onPressed: () {},
                      elevation: 0,
                      splashColor: Colors.transparent,
                      child: Text("Login",
                          style: TextStyle(
                              color: Base.colors["colorAccent"],
                              backgroundColor: Colors.transparent)),
                    ),
                  ],
                ),
              )
            ])));
  }

  @override
  void initState() {
    super.initState();
  }

  void fetchServices() async {
    adverts = await apiServices.getAdverts();
  }
}
