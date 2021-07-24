<template>

<v-data-table
    :headers="headers"
    :items="mypage"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>

</template>

<script>
  const axios = require('axios').default;

  export default {
    name: 'Mypage',
    props: {
      value: Object,
      editMode: Boolean,
      isNew: Boolean
    },
    data: () => ({
        headers: [
            { text: "id", value: "id" },
            { text: "memberName", value: "memberName" },
            { text: "exhibitionId", value: "exhibitionId" },
            { text: "reservationId", value: "reservationId" },
            { text: "voucherId", value: "voucherId" },
            { text: "exhibitionName", value: "exhibitionName" },
            { text: "exhibitionStatus", value: "exhibitionStatus" },
            { text: "exhibitionDate", value: "exhibitionDate" },
            { text: "exhibitionType", value: "exhibitionType" },
        ],
        mypage : [],
    }),
    async created() {
      var temp = await axios.get(axios.backend + '/mypages')

      this.mypage = temp.data._embedded.mypages;

    },
    methods: {
    }
  }
</script>

