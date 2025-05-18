import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.deepsea.data.api.RetrofitClient
import com.example.deepsea.data.model.user.UserProfileData
import com.example.deepsea.utils.Resource
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException

class UserProfileViewModel : ViewModel() {
    // Keep the original state to maintain backward compatibility with existing UI components
    private val _userProfileData = mutableStateOf<UserProfileData?>(null)
    val userProfileData: State<UserProfileData?> = _userProfileData

    // Add new state wrapper with Resource
    private val _profileResource = mutableStateOf<Resource<UserProfileData>>(Resource.Loading())
    val profileResource: State<Resource<UserProfileData>> = _profileResource

    fun fetchUserProfile(userId: Long?) {
        viewModelScope.launch {
            try {
                _profileResource.value = Resource.Loading(_userProfileData.value)

                val data = RetrofitClient.userProfileService.getUserProfileById(userId)
                _userProfileData.value = data
                _profileResource.value = Resource.Success(data)
            } catch (e: ConnectException) {
                // Connection error (server unreachable)
                _profileResource.value = Resource.Error(
                    "Unable to connect to server. Please check your connection.",
                    _userProfileData.value
                )
            } catch (e: SocketTimeoutException) {
                // Timeout error
                _profileResource.value = Resource.Error(
                    "Connection timed out. Please try again later.",
                    _userProfileData.value
                )
            } catch (e: IOException) {
                // Network error
                _profileResource.value = Resource.Error(
                    "Network error: ${e.message}",
                    _userProfileData.value
                )
            } catch (e: Exception) {
                // General exception
                _profileResource.value = Resource.Error(
                    "An error occurred: ${e.message}",
                    _userProfileData.value
                )
            }
        }
    }
}
