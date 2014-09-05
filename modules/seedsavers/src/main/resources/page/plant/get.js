var site = portal.siteContent;
var editMode = portal.request.mode == 'edit';

var thymeleaf = require('view/thymeleaf');
var view = resolve('./plant.page.html');
var params = {
    context: portal,
    site: portal.siteContent,
    content: portal.content,
    pageRegions: portal.pageRegions,
    mainRegion: portal.pageRegions.getRegion("main"),
    editable: editMode,
    from: "plant"
};
var body = thymeleaf.render(view, params);

portal.response.contentType = 'text/html';
portal.response.body = body;


