package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.forms.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

import static java.awt.SystemColor.menu;

/**
 * Created by Shoba on 4/11/2017.
 */
@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model){

        model.addAttribute("title","Menu List");
        model.addAttribute("menus", menuDao.findAll());
        return "menu/index";
    }

    @RequestMapping(value="add", method= RequestMethod.GET)
    public String add(Model model){

        model.addAttribute("title", "Add New Menu");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value="add", method = RequestMethod.POST)
    public String processAddMenuForm(@ModelAttribute @Valid Menu newMenu, Errors errors, Model model){

        if(errors.hasErrors()){

            model.addAttribute("title", "Add New Menu");
            return "menu/add";
        }

        menuDao.save(newMenu);

        return "redirect:view/" + newMenu.getId();
    }


    @RequestMapping(value = "view/{menuId}", method = RequestMethod.GET)
    public String viewMenu(Model model, @PathVariable int menuId){

        Menu theMenu = menuDao.findOne(menuId);
        model.addAttribute("title", theMenu.getName());
        model.addAttribute("cheeses", theMenu.getCheeses());
        model.addAttribute("menuId", theMenu.getId());

        return "menu/view";

    }

    @RequestMapping(value="add-item/{menuId}", method=RequestMethod.GET)
    public String addItem(Model model,
                          @PathVariable int menuId){


        AddMenuItemForm form = new AddMenuItemForm(menuDao.findOne(menuId), cheeseDao.findAll());
        model.addAttribute("form", form);
        model.addAttribute("title", "Add Cheese to " + menuDao.findOne(menuId).getName());
        return "menu/add-item";
    }


    @RequestMapping(value="add-item", method = RequestMethod.POST)
    public String addItem(Model model,
                          @ModelAttribute @Valid AddMenuItemForm form,
                          Errors errors){
        if(errors.hasErrors()){
            //model.addAttribute("title", "Add Cheese to " + menuDao.findOne(form.getMenuId()));
            model.addAttribute("form", form);
            return "menu/add-item" + form.getMenuId();
        }

        Cheese theCheese = cheeseDao.findOne(form.getCheeseId());
        Menu theMenu = menuDao.findOne(form.getMenuId());
        theMenu.addItem(theCheese);

        menuDao.save(theMenu);

        return "redirect:/menu/view/" + theMenu.getId();
    }


}


