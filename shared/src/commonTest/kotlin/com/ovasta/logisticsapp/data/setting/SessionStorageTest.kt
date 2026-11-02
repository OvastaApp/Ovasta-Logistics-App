package com.ovasta.logisticsapp.data.setting

import com.ovasta.logisticsapp.data.User
import com.ovasta.logisticsapp.data.setting.data.SettingsLocalDataSource
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionJson
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionPreferences
import com.ovasta.logisticsapp.data.setting.data.datastore.SessionStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class InMemorySessionStore(initial: SessionPreferences = SessionPreferences()) : SessionStore {
    private val state = MutableStateFlow(initial)
    override val data: Flow<SessionPreferences> = state
    override suspend fun update(transform: (SessionPreferences) -> SessionPreferences) =
        transform(state.value).also { state.value = it }
}

class SessionStorageTest {

    @Test
    fun savingAUserAlsoStoresItsTokenForRequests() = runTest {
        val store = InMemorySessionStore()
        val local = SettingsLocalDataSource(store)

        local.saveUserData(User(id = 5, deliveryId = 7, token = "abc"))

        val session = store.data.first()
        assertEquals(7, session.user?.deliveryId)
        assertEquals("abc", session.accessToken)
    }

    @Test
    fun clearingTheUserResetsTheSessionToArabic() = runTest {
        val store = InMemorySessionStore(
            SessionPreferences(userLang = "en", user = User(id = 5), accessToken = "abc", fcmToken = "fcm")
        )

        SettingsLocalDataSource(store).clearUserData()

        val session = store.data.first()
        assertNull(session.user)
        assertEquals("", session.accessToken)
        assertEquals("", session.fcmToken)
        assertEquals("ar", session.userLang)
    }

    @Test
    fun consentAndFcmTokenRoundTrip() = runTest {
        val local = SettingsLocalDataSource(InMemorySessionStore())

        local.setLocationConsentShown(true)
        local.saveFcmToken("fcm-1")

        assertTrue(local.isLocationConsentShown())
        assertEquals("fcm-1", local.getFcmToken())
    }

    @Test
    fun readsASessionWrittenBeforeTheMoveToShared() {
        // As the Android app stored it: default Json, User with its old camelCase keys
        val stored = """{"userLang":"ar","user":{"id":5,"deliveryId":7,"districtId":3,"name":"Ali",
            "mobile":"0100","email":null,"userTypeId":1,"available":true,"isOutsource":true,
            "token":"abc"},"accessToken":"abc","fcmToken":"fcm","locationConsentShown":true,
            "someRemovedField":1}"""

        val session = SessionJson.decode(stored)

        assertEquals(7, session.user?.deliveryId)
        assertEquals(3, session.user?.districtId)
        assertEquals("abc", session.accessToken)
        assertTrue(session.locationConsentShown)
        // User is not a data class, so compare what is written back
        val written = SessionJson.encode(session)
        assertEquals(written, SessionJson.encode(SessionJson.decode(written)))
    }
}
