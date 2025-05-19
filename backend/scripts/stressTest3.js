import http from 'k6/http';
import { sleep } from 'k6';


export const options = {
    stages: [
        { duration: '5m', target: 2000 }
    ],
};

export default function () {
    http.get('https://api.app2.mm.ts0608.life/api/monitoring/health');

    sleep(1);
}