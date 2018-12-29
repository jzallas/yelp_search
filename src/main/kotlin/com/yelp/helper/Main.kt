package com.yelp.helper

import okhttp3.OkHttpClient
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.file.Files
import java.nio.file.Paths

private const val API_KEY = "---PUT KEY HERE---"
private const val SEARCH_LOCATION = "NYC"
private const val SEARCH_TERM = "dim sum"
private const val SEARCH_LIMIT = 50
private const val SEARCH_OFFSET = 0

private val client: YelpApi
    get() {
        val client = OkHttpClient.Builder()
                .addInterceptor(AuthorizationInterceptor(API_KEY))
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.yelp.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(YelpApi::class.java)
    }

fun main(args: Array<String>) {
    val results = client.search(
            location = SEARCH_LOCATION,
            term = SEARCH_TERM,
            limit = SEARCH_LIMIT,
            offset = SEARCH_OFFSET
    )

    val response = results.execute()

    println("Response code: ${response.code()}")
    if (response.isSuccessful) {
        Files.newBufferedWriter(Paths.get("./yelp_export.csv")).use {
            val printer = CSVPrinter(it, CSVFormat.DEFAULT.withHeader(*Business.headers()))

            response.body()
                    ?.filter { business -> !business.isClosed }
                    ?.chunked(200)
                    ?.forEach { chunk ->
                        chunk.forEach { business ->
                            printer.printRecord(*business.values())
                        }
                        printer.flush()
                    }
        }
    } else {
        throw IllegalStateException("Failed request (${response.code()}): ${response.errorBody()?.string()}")
    }
}
