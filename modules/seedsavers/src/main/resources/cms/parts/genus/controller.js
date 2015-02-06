var thymeleaf = require('/cms/lib/view/thymeleaf');

function handleGet() {

    var reqContent = execute('portal.getContent');
    var reqComponent = execute('portal.getComponent');
    var view = resolve('./genus.html');
    var genus;
    if (reqContent.type == "portal:page-template") {
        genus = {
            scientificName: "Scientific name",
            norwegianNames: ["navn1", "navn2", "navn3"]
        };
    }
    else {
        genus = {
            scientificName: reqContent.displayName,
            norwegianNames: [].concat(reqContent.data.norwegianNames)
        };

    }

    var params = {
        component: reqComponent,
        content: reqContent,
        genus: genus
    };
    var body = thymeleaf.render(view, params);

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;
