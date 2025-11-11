Codes which are returned via the Account Linking SDK callbacks.

| Name     | Value | Description                                                                                                     |
|----------|-------|-----------------------------------------------------------------------------------------------------------------|
|PARSING_FAILURE| 400   | The structure of the retailer’s website or data feed that was encountered during parsing was unexpected         |
|INVALID_CREDENTIALS| 401  | Login failed on the retailer’s site due to invalid credentials                                                  |
|MISSING_CREDENTIALS| 1003  | No linked account for the specified retailer was found                                                          |
|VERIFICATION_NEEDED| 1004  | Login encountered a scenario requiring manual user intervention (CAPTCHA, 2FA, etc)                             |
|JS_INVALID_DATA| 1005  | The structure of the retailer’s website or data feed that was encountered during parsing was unexpected         |
|JS_CORE_LOAD_FAILURE| 1007  | An unexpected error occurred during login or parsing                                                            |
|USER_INPUT_COMPLETED| 1008  | The user completed the necessarry input after a VERIFICATION_NEEDED code was returned and the WebView was shown |
|INTERNAL_ERROR| 1009  | An unexpected error occurred during login or parsing                                                            |
|AUTHENTICATION_FAILURE| 1050  | Login failed on the merchant's site                                                                             |
|UNSUPPORTED_RETAILER| 1052  | This retailer is not currently supported                                                                        |
|ALREADY_LINKED| 2000  | This retailer already has linked account                                                                        |
|REAUTHENTICATION_REQUIRED| 2001  | Operation failed because the account requires re-authentication                                                 |
