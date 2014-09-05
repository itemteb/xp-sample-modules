var site = portal.siteContent;
var editMode = portal.request.mode == 'edit';

var thymeleaf = require('view/thymeleaf');
var view = resolve('./top-main.page.html');
var params = {
    context: portal,
    site: portal.siteContent,
    content: portal.content,
    pageRegions: portal.pageRegions,
    topRegion: portal.pageRegions.getRegion("top"),
    mainRegion: portal.pageRegions.getRegion("main"),
    editable: editMode,
    from: "site"
};
var body = thymeleaf.render(view, params);

portal.response.contentType = 'text/html';
portal.response.body = body;


