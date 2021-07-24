
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import ExhibitionManager from "./components/ExhibitionManager"

import ReservationManager from "./components/ReservationManager"


import Mypage from "./components/Mypage"
import VoucherManager from "./components/VoucherManager"

export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/exhibitions',
                name: 'ExhibitionManager',
                component: ExhibitionManager
            },

            {
                path: '/reservations',
                name: 'ReservationManager',
                component: ReservationManager
            },


            {
                path: '/mypages',
                name: 'Mypage',
                component: Mypage
            },
            {
                path: '/vouchers',
                name: 'VoucherManager',
                component: VoucherManager
            },



    ]
})
