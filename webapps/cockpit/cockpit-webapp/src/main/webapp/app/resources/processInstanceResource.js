ngDefine('cockpit.resources', function(module) {

  var Resource = [ '$resource', 'Uri', function ($resource, Uri) {

    return $resource(Uri.appUri('engine://engine/:engine/process-instance/:id/:action'), { id: '@id' }, {
      count: { method: 'GET', isArray: false, params: { id: 'count' }},
      activityInstances: { method: 'GET', isArray: false, params: { action: 'activity-instances' }}
    });
  }];

  module.factory('ProcessInstanceResource', Resource);

});
