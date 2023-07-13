package kg.kadyrbekov.controller;

import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/language")
@Api(tags = "Language API")
public class LanguageController {

    @ApiOperation(value = "Select language", notes = "Selects the preferred language for the session.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Language selected successfully"),
            @ApiResponse(code = 400, message = "Invalid language provided")
    })
    @PostMapping("/select-language")
    public String selectLanguage(
            @ApiParam(value = "Language code (en, ru)", required = true) @RequestParam(value = "lang", required = false) String lang,
            HttpSession session
    ) {
        if (lang == null || lang.isEmpty()) {
            lang = "ru";
        }
        session.setAttribute("language", lang);
        return "OK";
    }

}


