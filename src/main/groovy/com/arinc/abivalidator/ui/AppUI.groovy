package com.arinc.abivalidator.ui

import com.arinc.abivalidator.Abi
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.vaadin.annotations.Widgetset
import com.vaadin.server.Sizeable
import com.vaadin.server.VaadinRequest
import com.vaadin.shared.ui.ContentMode
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.ui.Button
import com.vaadin.ui.Label
import com.vaadin.ui.TextArea
import com.vaadin.ui.UI
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import groovy.json.JsonSlurper

@SpringUI
//@Theme('app')
@Widgetset('app')
class AppUI extends UI {


    @Override
    protected void init(VaadinRequest vaadinRequest) {
        def layout = new VerticalLayout()

        layout.addComponent(new Label('ABI Validator').tap {
            styleName = ValoTheme.LABEL_H1
        })

        TextArea area = new TextArea("ABI content")
        area.setWordWrap(false)
        area.setSizeFull()
        area.setHeight(500, Sizeable.Unit.PIXELS)
        layout.addComponent(area)
        def errorsOut = new Label("")
        errorsOut.setWidth("800px")
        errorsOut.contentMode = ContentMode.PREFORMATTED

        layout.addComponent(new Button('Validate', {
            try {

                ObjectMapper objectMapper = new ObjectMapper()
                def abi = objectMapper.readValue(area.value, Abi.class)
                def output = ""
                output += "Declared types (${abi.declaredTypes.size()}): $abi.declaredTypes\n"
                output += "Types in structs (${abi.structTypes.size()}): $abi.structTypes\n"
                output += "Structs (${abi.structNames.size()}): $abi.structNames\n"
                output += "Actions (${abi.actionNames.size()}): $abi.actionNames\n"
                output += "Tables (${abi.tableNames.size()}): $abi.tableNames\n"

                def report = abi.validate()
                if (report) {
                    output += "Errors count: ${report.size()}\n"
                    output += report.join("\n")
                } else {
                    output += "No errors\n"
                }

                errorsOut.value = output.readLines().collect {
                    wrapLine(it, 150)
                }.join("\n")

            } catch (JsonMappingException | JsonParseException ex) {
                errorsOut.value = formatError(ex)
                area.setCursorPosition(ex.getLocation().getCharOffset() as int)
            }
        } as Button.ClickListener))

        layout.addComponent(new Label('Errors:').tap {
            styleName = ValoTheme.LABEL_H3
        })
        layout.addComponent(errorsOut)
        setContent(layout)
    }

    static String formatError(Exception ex) {
        return """
Invalid JSON: ${ex.getOriginalMessage()}
line number: ${ex.getLocation().getLineNr()}
index number: ${ex.getLocation().getColumnNr()}
"""
    }

    static String wrapLine(String text, int maxLineSize) {
        def words = text.split(/\s+/)
        def lines = ['']
        words.each { word ->
            def lastLine = (lines[-1] + ' ' + word).trim()
            if (lastLine.size() <= maxLineSize) {
                // Change last line.
                lines[-1] = lastLine
            } else {
                // Add word as new line.
                lines << word
            }
        }
        return lines.join("\n    ")
    }

}
