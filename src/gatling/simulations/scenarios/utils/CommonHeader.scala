package utils

object CommonHeader {
 val baseURL = Environment.baseURL
 val IdamUrl = Environment.idamURL

 // below headers are for home page and login and logout headers

 val headers_homepage = Map(
  "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "sec-fetch-dest" -> "document",
  "sec-fetch-mode" -> "navigate",
  "sec-fetch-site" -> "none",
  "sec-fetch-user" -> "?1",
  "upgrade-insecure-requests" -> "1")

 val headers_1 = Map(
  "accept" -> "text/css,*/*;q=0.1",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "sec-fetch-dest" -> "style",
  "sec-fetch-mode" -> "no-cors",
  "sec-fetch-site" -> "same-origin")

 val headers_2 = Map(
  "accept" -> "*/*",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "sec-fetch-dest" -> "script",
  "sec-fetch-mode" -> "no-cors",
  "sec-fetch-site" -> "same-origin")

 val headers_8 = Map(
  "accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "sec-fetch-dest" -> "image",
  "sec-fetch-mode" -> "no-cors",
  "sec-fetch-site" -> "same-origin")

 val headers_login = Map(
  "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "cache-control" -> "max-age=0",
  "content-type" -> "application/x-www-form-urlencoded",
  "origin" -> IdamUrl,
  "sec-fetch-dest" -> "document",
  "sec-fetch-mode" -> "navigate",
  "sec-fetch-site" -> "same-origin",
  "sec-fetch-user" -> "?1",
  "upgrade-insecure-requests" -> "1")

 val headers_23 = Map(
  "accept" -> "*/*",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "if-modified-since" -> "Mon, 27 Jul 2020 12:00:00 GMT",
  "sec-fetch-dest" -> "script",
  "sec-fetch-mode" -> "no-cors",
  "sec-fetch-site" -> "cross-site")

 val headers_29 = Map(
  "accept" -> "*/*",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "origin" -> s"${baseURL}",
  "sec-fetch-dest" -> "font",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin")

 val headers_30 = Map(
  "accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "sec-fetch-dest" -> "image",
  "sec-fetch-mode" -> "no-cors",
  "sec-fetch-site" -> "cross-site")

 val headers_bulkscanfeature = Map(
  "accept" -> "application/json, text/plain, */*",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-GB,en-US;q=0.9,en;q=0.8",
  "csrf-token" -> "#{csrf}",
  "sec-ch-ua" -> """Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87""",
  "sec-ch-ua-mobile" -> "?0",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-requested-with" -> "XMLHttpRequest")

 val headers_viewreports = Map(
  "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "sec-fetch-dest" -> "document",
  "sec-fetch-mode" -> "navigate",
  "sec-fetch-site" -> "same-origin",
  "sec-fetch-user" -> "?1",
  "upgrade-insecure-requests" -> "1")

 val headers_reports = Map(
  "accept" -> "application/json, text/plain, */*",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "csrf-token" -> "#{csrf}",
  "sec-fetch-dest" -> "empty",
  "sec-fetch-mode" -> "cors",
  "sec-fetch-site" -> "same-origin",
  "x-requested-with" -> "XMLHttpRequest")


 val headers_logout = Map(
  "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "sec-fetch-dest" -> "document",
  "sec-fetch-mode" -> "navigate",
  "sec-fetch-site" -> "same-origin",
  "sec-fetch-user" -> "?1",
  "upgrade-insecure-requests" -> "1")
}
