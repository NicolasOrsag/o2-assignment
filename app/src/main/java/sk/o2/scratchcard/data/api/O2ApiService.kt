package sk.o2.scratchcard.data.api

import retrofit2.http.GET
import retrofit2.http.Query

data class VersionResponse(
    val android: String
)

interface O2ApiService {
    @GET("version")
    suspend fun getVersion(@Query("code") code: String): VersionResponse
}
