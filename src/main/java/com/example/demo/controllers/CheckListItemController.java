package com.example.demo.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CheckList", description = "Operations related to users' checklist items")
@RestController
@RequestMapping("/checklistsItem")
public class CheckListItemController {
}
