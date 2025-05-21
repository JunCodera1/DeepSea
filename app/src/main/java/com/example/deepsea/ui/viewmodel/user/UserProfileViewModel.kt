import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.user.UserProfile
import com.example.deepsea.utils.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class UserProfileViewModel : ViewModel() {
    // Keep the original state to maintain backward compatibility with existing UI components
    private val _userProfile = mutableStateOf<UserProfile?>(null)
    val userProfile: State<UserProfile?> = _userProfile

    // Add new state wrapper with Resource
    private val _profileResource = mutableStateOf<Resource<UserProfile>>(Resource.Loading())
    val profileResource: State<Resource<UserProfile>> = _profileResource

    fun fetchUserProfile(userId: Long?) {
        if (userId == null) {
            _profileResource.value = Resource.Error("Invalid user ID.", _userProfile.value)
            return
        }

        viewModelScope.launch {
            try {
                _profileResource.value = Resource.Loading(_userProfile.value)
                val data = RetrofitClient.userProfileService.getUserProfileById(userId)
                _userProfile.value = data
                _profileResource.value = Resource.Success(data)
            } catch (e: Exception) {
                handleException(e)
            }
        }
    }

    private fun handleException(e: Exception) {
        val message = when (e) {
            is ConnectException -> "Unable to connect to server. Please check your connection."
            is SocketTimeoutException -> "Connection timed out. Please try again later."
            is IOException -> "Network error: ${e.message}"
            else -> "An error occurred: ${e.message}"
        }
        _profileResource.value = Resource.Error(message, _userProfile.value)
    }

}
