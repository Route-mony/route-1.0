import 'dart:convert';

import 'package:http/http.dart';
import 'package:route_flutter/api/configs.dart';
import 'package:route_flutter/models/adverts/advert.dart';

class ApiServices {
  Future<List<Advert>> getAdverts() async {
    try {
      Response response = await get(Configs.BASE_URL + "advert/list/all");
      return jsonDecode(response.body)["data"]["rows"]
          .map<Advert>((data) => Advert.fromMap(data))
          .toList();
    } catch (e) {
      print(e);
      return [];
    }
  }
}
