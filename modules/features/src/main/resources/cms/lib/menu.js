function isInteger(x) {
    return Math.round(x) === x;
}

function isMenuItem(content) {
    var meta = content.meta;
    if (!meta) {
        return false;
    }
    var menuItemMetadata = meta['features-menu-item'] || {};
    var menuItemValue = menuItemMetadata.menuItem;
    return menuItemValue && menuItemValue[0];
}

function getChildMenuItems(parentContent, levels) {
    var childrenResult = execute('content.getChildren', {
        key: parentContent._id,
        count: 100
    });

    levels--;

    var childMenuItems = [];
    
    childrenResult.contents.forEach(function (child) {
        if (isMenuItem(child)) {
            childMenuItems.push(menuItemToJson(child, levels));
        }
    });

    log.info('getChildMenuItems: \r\n %s', JSON.stringify(childMenuItems, null, 4));
    
    return childMenuItems;
}

function menuItemToJson(content, levels) {
    var subMenus = [];
    if (levels > 0) {
        subMenus = getChildMenuItems(content, levels);
    }
    
    return {
        displayName: content.displayName,
        menuName: content.meta['features-menu-item'].menuName[0],
        path: content._path,
        name: content._name,
        id: content._id,
        hasChildren: subMenus.length > 0,
        children: subMenus
    };
}

exports.getSiteMenu = function (levels) {
    levels = (isInteger(levels) ? levels : 1);
    var site = execute('portal.getSite');
    if (!site) {
        return [];
    }
    var menuItems = getChildMenuItems(site, levels);

    log.info('Site Menu: \r\n %s', JSON.stringify(menuItems, null, 4));

    return menuItems;
};
