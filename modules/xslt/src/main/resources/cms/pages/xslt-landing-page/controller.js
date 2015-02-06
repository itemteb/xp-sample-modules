var view = resolve('/cms/view/frogger.xsl');

function handleGet(req) {

    var editMode = req.mode == 'edit';
    var model = {
        title: req.content.displayName,
        editable: editMode
    };

    var body = execute('xslt.render', {
        view: view,
        model: model
    });

    return {
        contentType: 'text/html',
        body: body
    };
}

exports.get = handleGet;
