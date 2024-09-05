Codes which are returned via the Account Linking SDK callbacks when calling `verify` or `orders`.

| Name     | Value | Description                                                                                                    |
|----------|-------|----------------------------------------------------------------------------------------------------------------|
|MISSING_CREDENTIALS| 1003  | An attempt was made to grab orders or verify but no linked account for the specified retailer was found        |
|VERIFICATION_NEEDED| 1004  | Login encountered a scenario requiring manual user intervention (CAPTCHA, 2FA, etc)                            |
|JS_INVALID_DATA| 1005  | The structure of the retailer’s website or data feed that was encountered during parsing was unexpected        |
|JS_CORE_LOAD_FAILURE| 1007  | An unexpected error occurred during login or parsing                                                           |
|USER_INPUT_COMPLETED| 1008  | The user completed the necessarry input after a VERIFICATION_NEEDED code was returned and the WebView was shown |
|PARSING_FAILURE| 1050  | The structure of the retailer’s website or data feed that was encountered during parsing was unexpected        |
|INVALID_CREDENTIALS| 1051  | Login failed on the retailer’s site due to invalid credentials                                                 |
|INTERNAL_ERROR| 1009  | An unexpected error occurred during login or parsing                                                           |
|UNSUPPORTED_RETAILER| 1052  | This retailer is not currently supported                                                           |
