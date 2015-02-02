var thymeleaf = require('/lib/view/thymeleaf');

function handleGet(req) {

    var reqContent = execute('portal.getContent');
    var reqComponent = execute('portal.getComponent');
    var view = resolve('./family.html');
    var family;
    if (reqContent.type == "portal:page-template") {
        family = {
            scientificName : "Scientific name",
            norwegianNames: ["navn1", "navn2", "navn3"]
        };
    }
    else {
        family = {
            scientificName : reqContent.displayName,
            norwegianNames: [].concat(reqContent.data.norwegianNames)
        };
    }

    var params = {
        component: reqComponent,
        content: reqContent,
        family: family
    };
    var body = thymeleaf.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;
