import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.SurveyOptionType
import com.example.deepsea.data.model.SurveyUpdateRequest
import com.example.deepsea.data.model.UserProfileData
import com.example.deepsea.utils.SessionManager
import kotlinx.coroutines.flow.first

class UserProfileViewModel : ViewModel() {
    private val _userProfileData = mutableStateOf<UserProfileData?>(null)
    val userProfileData: State<UserProfileData?> = _userProfileData

    fun fetchUserProfile(userId: Long?) {
        viewModelScope.launch {
            try {
                val data = RetrofitClient.userProfileService.getUserProfileById(userId)
                _userProfileData.value = data
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun updateSurveySelections(
        selected: Set<String>,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit,
        sessionManager: SessionManager
    ) {
        viewModelScope.launch {
            try {
                val userId = sessionManager.userId.first()
                if (userId != null) {
                    val optionTypes = selected.mapNotNull {
                        try {
                            SurveyOptionType.valueOf(it.uppercase()) // convert back from displayName
                        } catch (e: IllegalArgumentException) {
                            null
                        }
                    }.toSet()

                    val request = SurveyUpdateRequest(
                        selectedSurveys = optionTypes.map { it.name }.toSet()
                    )
                    RetrofitClient.userProfileService.updateSurveySelections(request)
                    fetchUserProfile(userId)
                    onSuccess()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e)
            }
        }
    }
}
