class Advert {
  final String title;
  final String description;
  final String url;

  Advert({this.title, this.description, this.url});

  factory Advert.fromMap(Map<String, dynamic> json) => Advert(
      title: json["title"],
      description: json["description"],
      url: json["content_url"]);
}
