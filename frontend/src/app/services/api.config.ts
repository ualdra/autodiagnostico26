const isAngularDevServer = typeof window !== 'undefined' && window.location.port === '4200';

export const API_BASE_URL = isAngularDevServer
	? 'http://localhost:8081/api'
	: 'http://localhost:7777/api';
