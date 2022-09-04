package diary.capstone.util.exception

import org.springframework.validation.BindingResult

class ValidationException(var bindingResult: BindingResult): RuntimeException("validation error")